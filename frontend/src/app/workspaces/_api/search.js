"use client";

import { useQuery } from "@tanstack/react-query";
import { API_BASE } from "@/lib/utils";

export const search = async (params) => {
  const response = await fetch(`${API_BASE}/users/find?search=${params}`, {
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  });
  return await response.json();
};

export const useSearch = (params) => {
  const { data = [], ...rest } = useQuery({
    queryKey: [`search/${params}`],
    queryFn: () => search(params),
    onError: () => {
      toast({
        title: "Coś poszło nie tak.",
        description: "Wystąpił problem podczas pobierania.",
      });
    },
    enabled: !!params,
  });

  return { data, ...rest };
};
