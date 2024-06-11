"use client";

import { useQuery } from "@tanstack/react-query";
import { useToast } from "@/components/ui/use-toast";
import { API_BASE } from "@/lib/utils";
import { useParams } from "next/navigation";

export const getIssues = async (workspaceId) => {
  const response = await fetch(`${API_BASE}/workspaces/${workspaceId}/issues`, {
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  });
  return await response.json();
};

export const useIssues = () => {
  const { toast } = useToast();
  const { id } = useParams();

  const { data = [], ...restQuery } = useQuery({
    queryKey: [`${id}/issues`],
    queryFn: () => getIssues(id),
    onError: () => {
      toast({
        title: "Coś poszło nie tak.",
        description: "Wystąpił problem podczas pobierania.",
      });
    },
  });

  return { issues: data, ...restQuery };
};

export const useSingleIssue = (issueId) => {
  const { issues, ...rest } = useIssues();

  return { issue: issues.find(({ id }) => id === issueId), ...rest };
};
