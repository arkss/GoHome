from rest_framework import permissions


class MyIsAuthenticated(permissions.BasePermission):
    message = ''

    def has_permission(self, request, view):
        if not bool(request.user):
            self.message = '유저가 존재하지 않습니다.'
            return False

        elif not bool(request.user.is_authenticated):
            self.message = '로그인이 필요합니다.'
            return False

        elif bool(request.user.status == '0'):
            self.message = '이메일 인증이 필요합니다.'
            return False
        return True
