from rest_framework import serializers as sz
from django.contrib.auth import get_user_model


class ProfileSeriallizer(sz.ModelSerializer):
    password = sz.CharField(write_only=True)

    def create(self, validated_data):
        Profile = get_user_model()
        password = validated_data.pop('password')
        profile = Profile(
            **validated_data
        )
        profile.set_password(password)
        profile.save()
        return profile

    class Meta:
        model = get_user_model()
        fields = ['username', 'password', 'email',
                  'nickname', 'address', 'detail_address']
