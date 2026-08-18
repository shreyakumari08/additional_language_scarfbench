import os
import django
from django.conf import settings
from django.http import HttpResponse, JsonResponse
from django.urls import path
from django.views.decorators.csrf import csrf_exempt

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

@csrf_exempt
def handler(request):
    name = (request.POST.get("name") or request.GET.get("name","world")).lower(); return HttpResponse(f"Hello, {name}")

@csrf_exempt
def root(request):
    return HttpResponse("OK")

urlpatterns = [
    path("response", handler),
    path("", root),
]

if __name__ == "__main__":
    from django.core.management import execute_from_command_line
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "__main__")
    execute_from_command_line(["manage.py", "runserver", "0.0.0.0:8080", "--noreload"])
