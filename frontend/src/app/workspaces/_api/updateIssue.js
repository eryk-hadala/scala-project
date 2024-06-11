"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useToast } from "@/components/ui/use-toast";
import { API_BASE } from "@/lib/utils";

export const updateIssue = async (issue) => {
  const response = await fetch(
    `${API_BASE}/workspaces/${issue.workspaceId}/issues/${issue.id}`,
    {
      method: "PUT",
      credentials: "include",
      body: JSON.stringify(issue),
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return await response.json();
};

export const useUpdateIssue = () => {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: updateIssue,
    onSuccess: (_, { workspaceId }) => {
      queryClient.invalidateQueries({
        queryKey: [`${workspaceId}/issues`],
      });
    },
    onError: () => {
      toast({
        title: "Coś poszło nie tak.",
        description: "Wystąpił problem podczas pobierania.",
      });
    },
  });
};
