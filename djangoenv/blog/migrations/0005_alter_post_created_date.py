# Generated by Django 3.2.21 on 2023-09-29 17:45

import datetime
from django.db import migrations, models
from django.utils.timezone import utc


class Migration(migrations.Migration):

    dependencies = [
        ('blog', '0004_auto_20230930_0226'),
    ]

    operations = [
        migrations.AlterField(
            model_name='post',
            name='created_date',
            field=models.DateTimeField(default=datetime.datetime(2023, 9, 29, 17, 45, 56, 700603, tzinfo=utc)),
        ),
    ]
