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

export const getSingleIssue = async (id, workspaceId) => {
  const response = await fetch(
    `${API_BASE}/workspaces/${workspaceId}/issues/${id}`,
    {
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
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

// export const useSingleIssue = () => {
//   const params = useParams();
//   const { toast } = useToast();

//   const { data, ...restQuery } = useQuery({
//     queryKey: [`issues/${params.id}`],
//     queryFn: () => getSingleWorkspace(params.id),
//     onError: () => {
//       toast({
//         title: "Coś poszło nie tak.",
//         description: "Wystąpił problem podczas pobierania.",
//       });
//     },
//   });

//   return { workspace: data, ...restQuery };
// };
