from rest_framework import permissions

class CustomIsAuthenticated(permissions.BasePermission):
    message = 'Adding customers not allowed.'

    def has_permission(self, request, view):
        return bool(request.user and request.user.is_authenticated)
