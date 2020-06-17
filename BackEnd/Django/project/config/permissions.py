from rest_framework import permissions

<<<<<<< HEAD

class MyIsAuthenticated(permissions.BasePermission):
    message = ''

    def has_permission(self, request, view):
        if request.user.is_anonymous:
            self.message = '유저가 존재하지 않습니다.'
            return False

        elif bool(request.user.status == '0'):
            self.message = '이메일 인증이 필요합니다.'
            return False

        elif not bool(request.user.is_authenticated):
            self.message = '로그인이 필요합니다.'
            return False

        return True
=======
class CustomIsAuthenticated(permissions.BasePermission):
    message = 'Adding customers not allowed.'

    def has_permission(self, request, view):
        return bool(request.user and request.user.is_authenticated)
>>>>>>> client/merge-AR
