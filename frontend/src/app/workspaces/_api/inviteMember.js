"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useToast } from "@/components/ui/use-toast";
import { API_BASE } from "@/lib/utils";

export const inviteMember = async ({ workspaceId, userId }) => {
  const response = await fetch(
    `${API_BASE}/workspaces/${workspaceId}/members`,
    {
      method: "POST",
      credentials: "include",
      body: JSON.stringify({ userId }),
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return await response.text();
};

export const useInviteMember = () => {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: inviteMember,
    onSuccess: (_, { workspaceId }) => {
      queryClient.invalidateQueries({
        queryKey: [`workspaces/${workspaceId}/members`],
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
