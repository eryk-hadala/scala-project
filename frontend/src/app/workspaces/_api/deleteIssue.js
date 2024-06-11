"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useToast } from "@/components/ui/use-toast";
import { API_BASE } from "@/lib/utils";
import { useParams } from "next/navigation";

export const deleteIssue = async (id, workspaceId) => {
  const response = await fetch(
    `${API_BASE}/workspaces/${workspaceId}/issues/${id}`,
    {
      method: "DELETE",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return await response.text();
};

export const useDeleteIssue = () => {
  const queryClient = useQueryClient();
  const { toast } = useToast();
  const { id: workspaceId } = useParams();

  return useMutation({
    mutationFn: (id) => {
      console.log(id, workspaceId);
      return deleteIssue(id, workspaceId);
    },
    onSuccess: () => {
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
