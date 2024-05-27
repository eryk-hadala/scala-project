"use client";

import { useQuery } from "@tanstack/react-query";
import { useToast } from "@/components/ui/use-toast";
import { API_BASE } from "@/lib/utils";
import { useParams } from "next/navigation";

export const getWorkspaces = async () => {
  const response = await fetch(`${API_BASE}/workspaces`, {
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  });
  return await response.json();
};

export const getSingleWorkspace = async (id) => {
  const response = await fetch(`${API_BASE}/workspaces/${id}`, {
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  });
  return await response.json();
};

export const useWorkspaces = () => {
  const { toast } = useToast();

  const { data = [], ...restQuery } = useQuery({
    queryKey: ["workspaces"],
    queryFn: getWorkspaces,
    onError: () => {
      toast({
        title: "Coś poszło nie tak.",
        description: "Wystąpił problem podczas pobierania.",
      });
    },
  });

  return { workspaces: data, ...restQuery };
};

export const useSingleWorkspace = () => {
  const params = useParams();
  const { toast } = useToast();

  const { data, ...restQuery } = useQuery({
    queryKey: [`workspaces/${params.id}`],
    queryFn: () => getSingleWorkspace(params.id),
    onError: () => {
      toast({
        title: "Coś poszło nie tak.",
        description: "Wystąpił problem podczas pobierania.",
      });
    },
  });

  return { workspace: data, ...restQuery };
};
