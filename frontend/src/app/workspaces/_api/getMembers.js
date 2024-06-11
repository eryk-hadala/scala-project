"use client";

import { useQuery } from "@tanstack/react-query";
import { API_BASE } from "@/lib/utils";
import { useParams } from "next/navigation";

export const getMembers = async (workspaceId) => {
  const response = await fetch(
    `${API_BASE}/workspaces/${workspaceId}/members`,
    {
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return await response.json();
};

export const useMembers = () => {
  const { id: workspaceId } = useParams();

  const { data = [], ...rest } = useQuery({
    queryKey: [`workspaces/${workspaceId}/members`],
    queryFn: () => getMembers(workspaceId),
    onError: () => {
      toast({
        title: "Coś poszło nie tak.",
        description: "Wystąpił problem podczas pobierania.",
      });
    },
  });

  return { members: data, ...rest };
};
