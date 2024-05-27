import { useRouter } from "next/navigation";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useToast } from "@/components/ui/use-toast";
import { API_BASE } from "@/lib/utils";

export const createWorkspace = async (body) => {
  const response = await fetch(`${API_BASE}/workspaces`, {
    method: "POST",
    body: JSON.stringify(body),
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  });
  return await response.json();
};

export const useCreateWorkspace = () => {
  const { toast } = useToast();
  const router = useRouter();
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: createWorkspace,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["workspaces"] });
      router.push(`/workspaces/${data.id}`);
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
