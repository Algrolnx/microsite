from django.urls import path
from .views import ProcessTaskView

urlpatterns = [
    path('process/', ProcessTaskView.as_view(), name='process-task'),
]