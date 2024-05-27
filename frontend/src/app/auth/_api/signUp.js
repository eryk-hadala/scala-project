import { useRouter } from "next/navigation";
import { useMutation } from "@tanstack/react-query";
import { useToast } from "@/components/ui/use-toast";
import { API_BASE } from "@/lib/utils";

export const signUp = async (body) => {
  const response = await fetch(`${API_BASE}/auth/sign-up`, {
    method: "POST",
    body: JSON.stringify({ ...body, avatarUrl: "" }),
    headers: {
      "Content-Type": "application/json",
    },
  });
  return await response.json();
};

export const useSignUp = () => {
  const { toast } = useToast();
  const router = useRouter();

  const mutation = useMutation({
    mutationFn: signUp,
    onSuccess: () => {
      toast({
        title: "Sukces.",
        description: "Konto zostało zarejestrowane.",
      });
      router.push("/auth/sign-in");
    },
    onError: () => {
      toast({
        title: "Coś poszło nie tak.",
        description: "Wystąpił problem podczas rejestracji.",
      });
    },
  });

  return mutation;
};
