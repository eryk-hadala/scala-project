"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useToast } from "@/components/ui/use-toast";
import { API_BASE } from "@/lib/utils";

export const removeWorkspace = async (workspace) => {
  const response = await fetch(`${API_BASE}/workspaces/${workspace.id}`, {
    method: "DELETE",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  });
  return await response.text();
};

export const useRemoveWorkspace = () => {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: removeWorkspace,
    onSuccess: (_, { id }) => {
      Promise.all([
        queryClient.invalidateQueries(["workspaces"]),
        queryClient.setQueryData([`workspaces/${id}`], null),
      ]);
    },
    onError: () => {
      toast({
        title: "Coś poszło nie tak.",
        description: "Wystąpił problem podczas pobierania.",
      });
    },
  });
};
