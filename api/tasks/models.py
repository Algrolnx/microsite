from django.db import models

class TaskRequest(models.Model):
    task_description = models.CharField(max_length=255)
    status = models.CharField(max_length=50, default='pending')
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Task {self.id}: {self.status}"
