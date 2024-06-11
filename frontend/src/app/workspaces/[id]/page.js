"use client";

import DataTable from "@/app/workspaces/_components/DataTable/DataTable";
import { useIssues } from "@/app/workspaces/_api/getIssues";

export default function Home() {
  const { issues } = useIssues();

  return (
    <div className="p-8">
      <DataTable data={issues} />
    </div>
  );
}
