use crate::validate::types::Framework;
use anyhow::Result;
use clap::Args;
use comfy_table::Table;
use std::path::{Path, PathBuf};
use walkdir::WalkDir;

/// Files whose presence marks a directory as a runnable framework variant.
///
/// The original ScarfBench keyed listing off `Makefile`, but the extension
/// tasks (Python/TypeScript/Rust and the new Java frameworks) are packaged with
/// language-native build descriptors and a containerized oracle instead. We
/// treat any of these markers as evidence of a variant so that no framework is
/// silently omitted from `list`.
const VARIANT_MARKERS: &[&str] = &[
    "Makefile",
    "makefile",
    "Dockerfile",
    "test.sh",
    "pom.xml",
    "build.gradle",
    "build.gradle.kts",
    "package.json",
    "Cargo.toml",
    "requirements.txt",
    "pyproject.toml",
];

/// True if `dir` directly contains any recognized build/oracle marker.
fn is_variant_dir(dir: &Path) -> bool {
    VARIANT_MARKERS.iter().any(|m| dir.join(m).is_file())
}

#[derive(Args, Debug)]
pub struct BenchListArgs {
    #[arg(long, help = "Path to the root of the scarf benchmark.")]
    pub benchmark_dir: String,

    #[arg(long, help = "Application layer to list.")]
    pub layer: Option<String>,
}

/// A simple list subcommand that lists all the benchmark applications as a table.
pub fn run(args: BenchListArgs) -> Result<i32> {
    // Get parse repository root
    let bench_root = PathBuf::from(args.benchmark_dir.as_str());
    assert!(
        bench_root.exists(),
        "This provided repository root {} doesn't exist?",
        bench_root.display()
    );
    log::debug!("Benchmark root: {}", bench_root.display());

    let base = match &args.layer {
        Some(layer) => bench_root.join(layer),
        None => bench_root.clone(),
    };

    if base.exists() {
        log::debug!("Base directory: {}", base.display());
    } else {
        anyhow::bail!(
            "The specified layer {} does not exist under base directory {}?",
            args.layer.as_deref().unwrap_or(""),
            base.display()
        );
    }

    let header = gen_header();
    match gen_rows(&base, &bench_root) {
        Ok(rows) => {
            // rows is fine, so let's use it!
            println!("{}", tabulate(&header, &rows));
        },
        Err(e) => {
            // Something wrong with the results, so log exception.
            log::error!("{e}");
        },
    };
    Ok(0)
}

/// Generate a header for the table.
///
/// A `Language` column is added between `Framework` and `Path` to surface the
/// language dimension of the extended benchmark. It is derived from the
/// framework name and shows `-` for directories whose framework we don't
/// recognize (so unknown variants are visible, not hidden).
fn gen_header() -> [String; 5] {
    [
        "Layer".to_string(),
        "Application".to_string(),
        "Framework".to_string(),
        "Language".to_string(),
        "Path".to_string(),
    ]
}

/// Generate the table rows.
///
/// A directory is listed as a variant when its relative path under the
/// benchmark root is exactly `layer/app/framework` and it contains a build or
/// oracle marker (see [`VARIANT_MARKERS`]). We iterate directories (not marker
/// files) so a variant is listed once regardless of how many markers it has.
fn gen_rows(
    base: &PathBuf,
    bench_root: &PathBuf,
) -> Result<Vec<[String; 5]>, anyhow::Error> {
    let mut rows: Vec<[String; 5]> = Vec::new();

    for entry in WalkDir::new(base).min_depth(1) {
        let entry = entry?;
        if !entry.file_type().is_dir() {
            continue;
        }
        let leaf = entry.path();

        // Only consider directories at the layer/app/framework depth.
        let rel = leaf.strip_prefix(bench_root)?;
        let parts: Vec<String> =
            rel.iter().map(|p| p.to_string_lossy().into_owned()).collect();
        if parts.len() != 3 {
            continue;
        }

        // Must actually be a runnable variant (build/oracle marker present).
        if !is_variant_dir(leaf) {
            continue;
        }

        let language = Framework::parse(&parts[2])
            .map(|f| f.language().to_string())
            .unwrap_or_else(|| "-".to_string());

        rows.push([
            parts[0].clone(),
            parts[1].clone(),
            parts[2].clone(),
            language,
            leaf.to_string_lossy().into_owned(),
        ]);
    }
    Ok(rows)
}

fn tabulate(header: &[String; 5], rows: &[[String; 5]]) -> Table {
    let mut table = Table::new();
    table.load_preset(comfy_table::presets::UTF8_FULL_CONDENSED);
    // Set header of the able
    table.set_header(header.to_vec());
    for row in rows {
        table.add_row(row.to_vec());
    }
    table
}

/// =====[ UNIT TESTS ]=====
#[cfg(test)]
mod tests {
    use super::*;
    use std::fs;
    use std::path::Path;

    fn _touch_makefile(dir: &Path) -> Result<()> {
        fs::create_dir_all(dir)?;
        fs::write(dir.join("Makefile"), "all:\n\techo Ok\n")?;
        Ok(())
    }

    /// Test to make sure the header is correct.
    #[test]
    fn test_gen_header() {
        let header = gen_header();
        assert_eq!(
            header,
            [
                "Layer".to_string(),
                "Application".to_string(),
                "Framework".to_string(),
                "Language".to_string(),
                "Path".to_string(),
            ]
        );
    }

    /// A recognized framework directory should be listed with its language,
    /// even when the only marker present is a Dockerfile (no Makefile) — this
    /// is the case for the Python/TypeScript/Rust extension tasks.
    #[test]
    fn test_recognizes_non_makefile_variant_and_language() -> Result<()> {
        let tmpdir = tempfile::tempdir().unwrap();
        let bench_root = tmpdir.path().join("benchmark");
        let variant = bench_root.join("business_domain/cart/axum");
        fs::create_dir_all(&variant)?;
        fs::write(variant.join("Dockerfile"), "FROM scratch\n")?;

        let rows = gen_rows(&bench_root, &bench_root).expect("gen_rows failed");
        assert_eq!(rows.len(), 1);
        assert_eq!(rows[0][0], "business_domain");
        assert_eq!(rows[0][1], "cart");
        assert_eq!(rows[0][2], "axum");
        assert_eq!(rows[0][3], "rust"); // language derived from framework
        Ok(())
    }

    /// A directory at the right depth but with no build/oracle marker must not
    /// be listed (avoids listing stray directories as variants).
    #[test]
    fn test_ignores_marker_less_dir() -> Result<()> {
        let tmpdir = tempfile::tempdir().unwrap();
        let bench_root = tmpdir.path().join("benchmark");
        fs::create_dir_all(bench_root.join("business_domain/cart/notes"))?;
        let rows = gen_rows(&bench_root, &bench_root).expect("gen_rows failed");
        assert!(rows.is_empty());
        Ok(())
    }

    /// Test generate rows for the table
    #[test]
    fn test_gen_rows() -> Result<()> {
        let tmpdir = tempfile::tempdir().unwrap();

        let repo_root = tmpdir.path();
        let bench_root = repo_root.join("benchmark");

        // Let's create a Makefile in the tempdir/benchmark/layer
        match _touch_makefile(&bench_root.join("layer/app/framework")) {
            Ok(()) => {
                log::info!("Created Makefile in layer/app/framework");
            },
            Err(e) => {
                log::error!("Failed to create Makefile: {}", e);
            },
        }

        // Let's also create another Makefile but now in layer/app.
        // This should not render in the table
        match _touch_makefile(&bench_root.join("layer/app")) {
            Ok(()) => {
                log::info!("Created Makefile in layer/app");
            },
            Err(e) => {
                log::error!("Failed to create Makefile: {}", e);
            },
        }

        let mut rows = gen_rows(&bench_root.join("layer"), &bench_root)
            .expect("gen_rows failed: {}");

        // Turns our walkdir can mangle ordering, so we have to manually order them
        rows.sort();
        assert_eq!(rows.len(), 1);
        assert_eq!(rows[0][0], "layer");
        assert_eq!(rows[0][1], "app");
        assert_eq!(rows[0][2], "framework");
        assert_eq!(rows[0][3], "-"); // unrecognized framework -> language "-"
        assert_eq!(
            rows[0][4],
            bench_root
                .join("layer/app/framework")
                .to_string_lossy()
                .into_owned()
        );
        Ok(())
    }

    /// Test that when I give a specific layer, I correctly ignore the other layers
    #[test]
    fn test_specify_benchroot() -> Result<()> {
        let tmpdir = tempfile::tempdir().unwrap();
        let repo_root = tmpdir.path();
        let bench_root = repo_root.join("benchmark");
        let base = bench_root.join("layer1");

        _touch_makefile(&bench_root.join("layer1/app1/framework"))?;
        _touch_makefile(&bench_root.join("layer2/app2/framework"))?;

        let mut rows =
            gen_rows(&base, &bench_root).expect("gen_rows failed: {}");
        rows.sort(); // Sort to preserve the row order

        assert_eq!(rows.len(), 1);
        assert_eq!(rows[0][0], "layer1");
        assert_eq!(rows[0][1], "app1");
        assert_eq!(rows[0][2], "framework");
        assert_eq!(rows[0][3], "-");
        assert_eq!(
            rows[0][4],
            bench_root
                .join("layer1/app1/framework")
                .to_string_lossy()
                .into_owned()
        );

        Ok(())
    }
}
