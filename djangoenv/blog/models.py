from django.conf import settings
from django.db import models
from django.utils import timezone


class Post(models.Model):
    author = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, blank=True, null=True)
    title = models.CharField(max_length=200)
    text = models.TextField()
    location = models.TextField(blank=True, null=True)
    created_date = models.DateTimeField(default=timezone.now(), blank=True, null=True)
    published_date = models.DateTimeField(default=timezone.now(), blank=True, null=True)
    image = models.ImageField(upload_to='intruder_image/%Y/%m/%d/')
    id = models.AutoField(primary_key=True)

    def publish(self):
        self.published_date = timezone.now()
        self.save()

    def __str__(self):
        return self.title
