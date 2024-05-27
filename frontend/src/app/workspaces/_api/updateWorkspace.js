import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useToast } from "@/components/ui/use-toast";
import { API_BASE } from "@/lib/utils";
import { useParams } from "next/navigation";

export const updateWorkspace = async (id, body) => {
  const response = await fetch(`${API_BASE}/workspaces/${id}`, {
    method: "PUT",
    body: JSON.stringify(body),
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  });
  return await response.json();
};

export const useUpdateWorkspace = () => {
  const params = useParams();
  const { toast } = useToast();
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: (body) => updateWorkspace(params.id, body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["workspaces"] });
      queryClient.invalidateQueries({ queryKey: [`workspaces/${params.id}`] });
    },
    onError: () => {
      toast({
        title: "Coś poszło nie tak.",
        description: "Wystąpił problem podczas aktualizowania.",
      });
    },
  });

  return mutation;
};
