"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useToast } from "@/components/ui/use-toast";
import { API_BASE } from "@/lib/utils";

export const assigneeIssue = async (issue) => {
  const response = await fetch(
    `${API_BASE}/workspaces/${issue.workspaceId}/issues/${issue.id}/assignees`,
    {
      method: "POST",
      body: JSON.stringify({ userIds: issue.assignees }),
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return await response.text();
};

export const useAssigneeIssue = () => {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: assigneeIssue,
    onSuccess: (_, { workspaceId }) => {
      queryClient.invalidateQueries({ queryKey: [`${workspaceId}/issues`] });
    },
    onError: () => {
      toast({
        title: "Coś poszło nie tak.",
        description: "Wystąpił problem podczas pobierania.",
      });
    },
  });
};
