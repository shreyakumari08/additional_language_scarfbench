use bon::Builder;
use serde::{Deserialize, Serialize};
use strum_macros::Display;

/// This captures the conversion status
#[derive(Clone, Debug, Deserialize, Serialize)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")] // Nice to see
pub enum Status {
    Converted,
    Completed,
    Prepared,
    Validated,
    Failed,
}
/// The programming language a framework belongs to.
///
/// ScarfBench (the paper) is Java-only across Spring/Jakarta/Quarkus. This
/// extension keeps that intact and adds three more Java frameworks plus three
/// sibling frameworks in each of Python, TypeScript, and Rust. Migrations are
/// always *within* a language (cross-framework, not cross-language), so a
/// task's language is well-defined by either endpoint framework.
#[derive(Clone, Copy, Debug, PartialEq, Eq, Hash, Deserialize, Serialize, Display)]
#[serde(rename_all = "snake_case")]
#[strum(serialize_all = "snake_case")]
pub enum Language {
    Java,
    Python,
    Typescript,
    Rust,
}

impl Language {
    /// All languages, in a stable order.
    pub fn all() -> &'static [Language] {
        use Language::*;
        &[Java, Python, Typescript, Rust]
    }

    /// The frameworks that belong to this language.
    pub fn frameworks(&self) -> &'static [Framework] {
        use Framework::*;
        match self {
            Language::Java => {
                &[Spring, Quarkus, Jakarta, Micronaut, Helidon, Vertx]
            },
            Language::Python => &[Flask, Fastapi, Django],
            Language::Typescript => &[Express, Fastify, Nestjs],
            Language::Rust => &[Axum, Actix, Rocket],
        }
    }
}

/// This captures the source/target framework of a migration task.
///
/// Variants are serialized in `snake_case` (e.g. `spring`, `fastapi`,
/// `nestjs`), which keeps the original Spring/Quarkus/Jakarta metadata parsing
/// byte-for-byte backward compatible while extending recognition to every
/// framework shipped by this benchmark. Adding variants here is what makes the
/// `validate` command, metadata parsing, and reporting recognize the new
/// frameworks instead of failing to deserialize them.
#[derive(Clone, Copy, Debug, PartialEq, Eq, Hash, Deserialize, Serialize, Display)]
#[serde(rename_all = "snake_case")] // Nice to see
#[strum(serialize_all = "snake_case")]
pub enum Framework {
    // --- Java (original ScarfBench) ---
    Spring,
    Quarkus,
    Jakarta,
    // --- Java (extension) ---
    Micronaut,
    Helidon,
    Vertx,
    // --- Python ---
    Flask,
    Fastapi,
    Django,
    // --- TypeScript ---
    Express,
    Fastify,
    Nestjs,
    // --- Rust ---
    Axum,
    Actix,
    Rocket,
}

impl Framework {
    /// Every framework recognized by the harness, in a stable order.
    pub fn all() -> &'static [Framework] {
        use Framework::*;
        &[
            Spring, Quarkus, Jakarta, Micronaut, Helidon, Vertx, Flask, Fastapi,
            Django, Express, Fastify, Nestjs, Axum, Actix, Rocket,
        ]
    }

    /// The language this framework belongs to.
    pub fn language(&self) -> Language {
        use Framework::*;
        match self {
            Spring | Quarkus | Jakarta | Micronaut | Helidon | Vertx => {
                Language::Java
            },
            Flask | Fastapi | Django => Language::Python,
            Express | Fastify | Nestjs => Language::Typescript,
            Axum | Actix | Rocket => Language::Rust,
        }
    }

    /// Case-insensitive parse from a directory/metadata string. Returns `None`
    /// for anything that is not a recognized framework so callers can decide
    /// whether to warn (rather than silently ignore) an unknown directory.
    pub fn parse(s: &str) -> Option<Framework> {
        let needle = s.trim().to_ascii_lowercase();
        Framework::all().iter().copied().find(|f| f.to_string() == needle)
    }
}
#[derive(Debug, Serialize, Deserialize, Clone, Copy, Default)]
#[serde(rename_all = "UPPERCASE")]
pub enum ValidationOutcome {
    True,
    False,
    #[default]
    Unk,
}
/// Captures the expected schema of the metadata JSON file
#[derive(Debug, Clone, Serialize, Builder, Deserialize)]
pub struct Metadata {
    /// The status of the runs
    pub status: Status,
    /// Name of the agent
    pub agent: String,
    /// Name of the application
    pub app: String,
    /// Application layer
    pub layer: String,
    /// The repeat of pass at k value
    pub repeat: usize,
    /// The source framework
    pub source_framework: Framework,
    /// The target framework
    pub target_framework: Framework,
    /// Number of known test cases (skip if not present while reading)
    #[serde(default, skip_serializing_if = "Option::is_none")]
    pub num_smoke_tests: Option<u32>,
    /// Compile status
    #[serde(default)]
    pub compile_ok: ValidationOutcome,
    /// Deploy status
    #[serde(default)]
    pub deploy_ok: ValidationOutcome,
    /// Absolute number of tests that passed (None if unknown/not run)
    #[serde(default)]
    pub tests_passed: Option<u32>,
    /// Failure reason (if any)
    #[serde(default, skip_serializing_if = "Option::is_none")]
    pub failure_reason: Option<String>,
    /// Failure category for classification
    #[serde(default, skip_serializing_if = "Option::is_none")]
    pub failure_category: Option<FailureCategory>,
    /// Whether this run needs to be rerun due to inconclusive results
    #[serde(default, skip_serializing_if = "is_false")]
    pub inconclusive: bool,
    /// Proper agent name (e.g. "claude-code"), distinct from folder-derived `agent`
    #[serde(default, skip_serializing_if = "Option::is_none")]
    pub solution_name: Option<String>,
    /// LLM model identifier (e.g. "claude-opus-4-6")
    #[serde(default, skip_serializing_if = "Option::is_none")]
    pub model: Option<String>,
    /// Optional variant discriminator (e.g. "with-skills", "with-claude-md")
    #[serde(default, skip_serializing_if = "Option::is_none")]
    pub variant: Option<String>,
}

impl Metadata {
    /// The language of this migration task. Source and target frameworks always
    /// share a language (migrations are cross-framework, not cross-language),
    /// so we derive it from the source framework.
    pub fn language(&self) -> Language {
        self.source_framework.language()
    }
}

/// This struct holds the metadata.json of the tests we have in the smoke dir
#[derive(Debug, Clone, Serialize, Builder, Deserialize)]
pub struct SmokeTestMetadata {
    pub(crate) num_smoke_tests: u32,
}

fn is_false(b: &bool) -> bool {
    !b
}

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "snake_case")]
pub enum FailureCategory {
    CompileError,
    BuildConfigError,
    BuildFailure,
    DockerBuildError,
    DockerImageMissing,
    DockerRunError,
    ContainerConflict,
    DeployTimeout,
    DeployError,
    DeployFailure,
    AppStartupFailure,
    BuildOrDeployFailure,
    CompileDependency,
    DeployDependency,
    TestFailure,
    TestFailures,
    TestParseError,
    TestTimeoutOom,
    NoTestOutput,
    ValidationTruncated,
    ProcessTerminated,
    Timeout,
    MissingLog,
    Unknown,
}

/// New types for leaderboard
#[derive(Debug, Serialize, Deserialize)]
pub struct Leaderboard {
    pub solution: LeaderboardSolution,
    pub results: Vec<LeaderboardResults>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct LeaderboardSolution {
    pub agent: String,
    pub model: String,
    #[serde(default, skip_serializing_if = "Option::is_none")]
    pub variant: Option<String>,
    pub date: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct LeaderboardResults {
    pub from: String,
    pub to: String,
    pub layer: String,
    pub app: String,
    pub repeats: Vec<Repeat>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Repeat {
    pub compile: bool,
    pub run: bool,
    pub tests_passed: u32,
    pub tests_total: u32,
}

/// =====[ UNIT TESTS ]=====
#[cfg(test)]
mod tests {
    use super::*;

    /// The three original ScarfBench frameworks must still round-trip exactly
    /// as before — this is the backward-compatibility guarantee.
    #[test]
    fn original_frameworks_still_parse() {
        for (json, fw) in [
            ("\"spring\"", Framework::Spring),
            ("\"quarkus\"", Framework::Quarkus),
            ("\"jakarta\"", Framework::Jakarta),
        ] {
            let parsed: Framework = serde_json::from_str(json).unwrap();
            assert_eq!(parsed, fw);
            assert_eq!(serde_json::to_string(&fw).unwrap(), json);
            assert_eq!(fw.language(), Language::Java);
        }
    }

    /// Every extension framework must deserialize from its lowercase directory
    /// name and map to the right language — nothing silently ignored.
    #[test]
    fn extension_frameworks_parse_and_map_language() {
        let cases = [
            ("micronaut", Framework::Micronaut, Language::Java),
            ("helidon", Framework::Helidon, Language::Java),
            ("vertx", Framework::Vertx, Language::Java),
            ("flask", Framework::Flask, Language::Python),
            ("fastapi", Framework::Fastapi, Language::Python),
            ("django", Framework::Django, Language::Python),
            ("express", Framework::Express, Language::Typescript),
            ("fastify", Framework::Fastify, Language::Typescript),
            ("nestjs", Framework::Nestjs, Language::Typescript),
            ("axum", Framework::Axum, Language::Rust),
            ("actix", Framework::Actix, Language::Rust),
            ("rocket", Framework::Rocket, Language::Rust),
        ];
        for (name, fw, lang) in cases {
            let parsed: Framework =
                serde_json::from_str(&format!("\"{name}\"")).unwrap();
            assert_eq!(parsed, fw, "deserialize {name}");
            assert_eq!(fw.to_string(), name, "display {name}");
            assert_eq!(fw.language(), lang, "language of {name}");
            assert_eq!(Framework::parse(name), Some(fw), "parse {name}");
        }
    }

    /// `Framework::all()` must exactly cover the union of each language's
    /// frameworks (15 total) with no gaps or duplicates.
    #[test]
    fn all_frameworks_partitioned_by_language() {
        assert_eq!(Framework::all().len(), 15);
        let mut from_langs: Vec<Framework> = Language::all()
            .iter()
            .flat_map(|l| l.frameworks().iter().copied())
            .collect();
        let mut all: Vec<Framework> = Framework::all().to_vec();
        from_langs.sort_by_key(|f| f.to_string());
        all.sort_by_key(|f| f.to_string());
        assert_eq!(all, from_langs);
        // Every framework's language must list it back.
        for f in Framework::all() {
            assert!(f.language().frameworks().contains(f));
        }
    }

    #[test]
    fn parse_is_case_insensitive_and_rejects_unknown() {
        assert_eq!(Framework::parse("SPRING"), Some(Framework::Spring));
        assert_eq!(Framework::parse("  Axum "), Some(Framework::Axum));
        assert_eq!(Framework::parse("cobol"), None);
    }

    /// A full metadata document with an extension framework must deserialize and
    /// report the correct language.
    #[test]
    fn metadata_with_extension_framework_deserializes() {
        let json = r#"{
            "status": "VALIDATED",
            "agent": "claude-code",
            "app": "cart",
            "layer": "business_domain",
            "repeat": 1,
            "source_framework": "express",
            "target_framework": "fastify"
        }"#;
        let m: Metadata = serde_json::from_str(json).unwrap();
        assert_eq!(m.source_framework, Framework::Express);
        assert_eq!(m.target_framework, Framework::Fastify);
        assert_eq!(m.language(), Language::Typescript);
    }
}
