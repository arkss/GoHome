from django.shortcuts import render
from django.contrib.auth import get_user_model
from django.http import JsonResponse
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.permissions import AllowAny
from rest_framework.decorators import api_view
from .serializers import ProfileSeriallizer

#from config.permissions import CustomIsAuthenticated

# email
from django.contrib.sites.shortcuts import get_current_site
from django.template.loader import render_to_string
from django.core.mail import EmailMessage
from django.utils.encoding import force_bytes, force_text
from .tokens import account_activation_token
from django.utils.http import urlsafe_base64_encode, urlsafe_base64_decode


class CreateProfileView(APIView):
    permission_classes = [AllowAny]

    def get(self, request, *args, **kwargs):
        queryset = get_user_model().objects.all()
        serializer = ProfileSeriallizer(queryset, many=True)

        return Response(serializer.data)

    def post(self, request, *args, **kwargs):
        """
        {
            "profile": {
                "username": "rkdalstjd9",
                "password": "qwe123",
                "email": "rkdalstjd9@naver.com",
                "nickname": "arkss"
            }
        }
        """
        data = request.data.get('profile')
        if not data:
            return Response({
                'response': 'error',
                'message': 'profile 파라미터가 없습니다.'
            })
        serializer = ProfileSeriallizer(data=data)
        if serializer.is_valid():
            profile = serializer.save()
        else:
            return Response({
                'response': 'error',
                'message': serializer.errors
            })

        return Response({
            'response': 'success',
            'message': 'profile이 성공적으로 생성되었습니다.'
        })


@api_view(['GET'])
def login_test(request):
    return Response({'message': '로그인 성공'})


def send_email_for_active(request):
    """
    {
        'username': 'rkdalstjd9'
    }
    """
    username = request.data.get('username')
    if not username:
        return Response({
            'response': 'error',
            'message': 'username 파라미터가 없습니다.'
        })

    User = get_user_model()
    try:
        profile = User.objects.get(username=username)
    except:
        return Response({
            'response': 'error',
            'message': f'{username}이 존재하지 않습니다'
        })

    current_site = get_current_site(request)
    message = render_to_string(
        'core/email_for_active.html',
        {
            'domain': current_site.domain,
            'profile_id': urlsafe_base64_encode(force_bytes(profile.id)).encode().decode(),
            'token': account_activation_token.make_token(profile),
        }
    )

    mail_title = "[GoHome] 회원가입 인증 메일입니다."
    user_email = profile.email
    email = EmailMessage(
        mail_title,
        message,
        to=[user_email]
    )
    email_result = email.send()
    if email_result:
        return Response({
            'response': 'success',
            'message': '메일 전송에 성공하였습니다.'
        })
    else:
        return Response({
            'response': 'error',
            'message': '메일 전송에 실패하였습니다.'
        })


def user_active(request, uid64, token):
    is_successed = True
    profile_id = int(force_text(urlsafe_base64_decode(uid64)))
    User = get_user_model()
    try:
        profile = User.objects.get(id=profile_id)
    except:
        is_successed = False

    if account_activation_token.check_token(profile, token):
        profile.status = '1'
        profile.save()
    else:
        is_successed = False

    context = {
        'is_successed': is_successed
    }

    return render(request, 'core/active_result.html', context)
