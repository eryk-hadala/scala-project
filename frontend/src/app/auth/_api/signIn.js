import { useMutation } from "@tanstack/react-query";
import { useToast } from "@/components/ui/use-toast";
import { API_BASE } from "@/lib/utils";
import { useRouter } from "next/navigation";

export const signIn = async (body) => {
  const response = await fetch(`${API_BASE}/auth/sign-in`, {
    method: "POST",
    body: JSON.stringify(body),
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  });
  return await response.json();
};

export const useSignIn = () => {
  const { toast } = useToast();
  const router = useRouter();

  const mutation = useMutation({
    mutationFn: signIn,
    onSuccess: () => {
      router.push(`/workspaces/1`);
    },
    onError: () => {
      toast({
        title: "Coś poszło nie tak.",
        description: "Wystąpił problem podczas logowania.",
      });
    },
  });

  return mutation;
};
