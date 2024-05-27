"use client";

import Navigation from "@/app/workspaces/_components/Navigation/Navigation";
import Sidebar from "@/app/workspaces/_components/Sidebar/Sidebar";
import { useSingleWorkspace, useWorkspaces } from "../_api/getWorkspaces";
import { ReloadIcon } from "@radix-ui/react-icons";
import NavigationSkeleton from "../_components/Navigation/NavigationSkeleton";
import SidebarSkeleton from "../_components/Sidebar/SidebarSkeleton";
import CreateWorkspaceDialog from "../_components/CreateWorkspaceDialog";
import { Button } from "@/components/ui/button";

export default function RootLayout({ children }) {
  const { workspaces, isLoading } = useWorkspaces();
  const { workspace, isLoading: isSingleLoading } = useSingleWorkspace();

  if (isLoading || isSingleLoading)
    return (
      <div className="flex items-center justify-center h-full">
        <ReloadIcon className="animate-spin" width={40} height={40} />
      </div>
    );

  if (!workspaces.length) {
    return (
      <div className="flex flex-col h-full">
        <NavigationSkeleton />
        <div className="flex flex-1 overflow-hidden">
          <SidebarSkeleton className="flex-shrink-0" />
          <main className="flex-1 flex flex-col justify-center items-center overflow-auto">
            <div className="flex flex-col items-center">
              <h3 className="font-semibold tracking-tight text-2xl">
                Nie znaleziono obszaru roboczego
              </h3>
              <p className="mt-2 mb-6 text-muted-foreground">
                Here is a list of your tasks for this month!
              </p>
              <CreateWorkspaceDialog renderTrigger={<Button>Utwórz</Button>} />
            </div>
          </main>
        </div>
      </div>
    );
  }

  if (!workspace) {
    return (
      <div className="flex flex-col h-full">
        <NavigationSkeleton variant="second" />
        <div className="flex flex-1 overflow-hidden">
          <SidebarSkeleton className="flex-shrink-0" />
          <main className="flex-1 flex flex-col justify-center items-center overflow-auto">
            <div className="flex flex-col items-center">
              <h3 className="font-semibold tracking-tight text-2xl">
                Nie znaleziono takiego obszaru roboczego
              </h3>
              <p className="mt-2 mb-6 text-muted-foreground">
                Wybierz swój obszar roboczy z listy w nawigacji
              </p>
            </div>
          </main>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full">
      <Navigation />
      <div className="flex flex-1 overflow-hidden">
        <Sidebar className="flex-shrink-0" />
        <main className="flex-1 overflow-auto">{children}</main>
      </div>
    </div>
  );
}
