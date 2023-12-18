from .models import Post
from rest_framework import serializers


class PostSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Post
        fields = ('title', 'text', 'created_date', 'location', 'published_date', 'image', 'id')

