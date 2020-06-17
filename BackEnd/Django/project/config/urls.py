from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('', include('core.urls')),
    path('route/', include('route.urls')),
    path('share/', include('share.urls')),
    path('api-auth/', include('rest_framework.urls')),
]
