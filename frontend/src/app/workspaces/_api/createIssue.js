import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useToast } from "@/components/ui/use-toast";
import { API_BASE } from "@/lib/utils";

export const createIssue = async (issue) => {
  const response = await fetch(
    `${API_BASE}/workspaces/${issue.workspaceId}/issues`,
    {
      method: "POST",
      body: JSON.stringify(issue),
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return await response.json();
};

export const useCreateIssue = () => {
  const { toast } = useToast();
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: createIssue,
    onSuccess: ({ workspaceId }) => {
      queryClient.invalidateQueries({ queryKey: [`${workspaceId}/issues`] });
    },
    onError: () => {
      toast({
        title: "Coś poszło nie tak.",
        description: "Wystąpił problem podczas tworzenia.",
      });
    },
  });

  return mutation;
};
