from django.urls import path
from . import views

urlpatterns = [
    path('', views.route_test),
    path('api/bikestops/<int:lat>/<int:lon>/<int:n>/', views.bike_stops),
    path('api/bikestop_parked_counts/', views.bikestop_parked_counts),
    path('routes/<int:lat_start>/<int:lon_start>/<int:lat_end>/<int:lon_end>/<str:include_bike>/<str:include_bus>/', views.routes),
]
