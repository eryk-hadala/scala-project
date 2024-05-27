"use client";

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useToast } from "@/components/ui/use-toast";
import { API_BASE } from "@/lib/utils";

export const authenticate = async () => {
  const response = await fetch(`${API_BASE}/auth/me`, {
    credentials: "include",
    cache: "no-store",
  });
  return await response.json();
};

export const updateAuthenticated = async (body) => {
  const response = await fetch(`${API_BASE}/auth/me`, {
    method: "POST",
    body: JSON.stringify(body),
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  });
  return await response.json();
};

export const useAuthenticate = () => {
  const { data, ...restQuery } = useQuery({
    queryKey: ["authenticate"],
    queryFn: authenticate,
    staleTime: Infinity,
  });

  return { user: data, ...restQuery };
};

export const useAuthenticateMutation = () => {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  const mutation = useMutation({
    mutationFn: updateAuthenticated,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["authenticate"] });
    },
    onError: () => {
      toast({
        title: "Coś poszło nie tak.",
        description: "Wystąpił problem podczas aktualizowania danych.",
      });
    },
  });

  return mutation;
};
