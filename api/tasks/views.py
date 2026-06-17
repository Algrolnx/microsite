import json
import redis
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from .models import TaskRequest

class ProcessTaskView(APIView):
    def post(self, request):
        payload = request.data.get("message", "Hard task by default")

        task_obj = TaskRequest.objects.create(task_description=payload)

        r = redis.Redis(host='redis', port=6379, db=0)

        task_data = json.dumps({
            "task": payload,
            "task_id": task_obj.id
            })

        r.lpush("task_queue", task_data)
    
        return Response(
            {"status": "Task added to the queue", "task_id": task_obj.id, "task": payload},
            status=status.HTTP_202_ACCEPTED
        )