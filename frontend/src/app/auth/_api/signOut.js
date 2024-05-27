import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useToast } from "@/components/ui/use-toast";
import { API_BASE } from "@/lib/utils";

export const signOut = async () => {
  await fetch(`${API_BASE}/auth/sign-out`, {
    method: "POST",
    credentials: "include",
  });
};

export const useSignOut = () => {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  const mutation = useMutation({
    mutationFn: signOut,
    onSuccess: () => {
      queryClient.resetQueries();
    },
    onError: () => {
      toast({
        title: "Coś poszło nie tak.",
        description: "Wystąpił problem podczas wylogowywania.",
      });
    },
  });

  return mutation;
};
