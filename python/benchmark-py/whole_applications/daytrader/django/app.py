import os
import django
from django.conf import settings
from django.http import HttpResponse, JsonResponse
from django.urls import path
from django.views.decorators.csrf import csrf_exempt

# DEGRADED: 14 KLOC trader app; Python REST quotes/portfolio DEGRADED

settings.configure(
    DEBUG=False,
    ALLOWED_HOSTS=["*"],
    ROOT_URLCONF=__name__,
    SECRET_KEY="benchmark-secret",
    INSTALLED_APPS=[],
    MIDDLEWARE=[],
    CACHES={"default": {"BACKEND": "django.core.cache.backends.locmem.LocMemCache"}},
)
django.setup()


def handler(request):
    return HttpResponse("<html><body><h1>daytrader</h1></body></html>")


def root(request):
    return HttpResponse("<html><body>OK</body></html>")

urlpatterns = [
    path("daytrader/", handler),
    path("", root),
]

if __name__ == "__main__":
    from django.core.management import execute_from_command_line
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "__main__")
    execute_from_command_line(["manage.py", "runserver", "0.0.0.0:9080", "--noreload"])
