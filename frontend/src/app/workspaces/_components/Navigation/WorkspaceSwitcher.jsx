"use client";

import * as React from "react";
import {
  CaretSortIcon,
  CheckIcon,
  PlusCircledIcon,
} from "@radix-ui/react-icons";

import { cn } from "@/lib/utils";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
  CommandSeparator,
} from "@/components/ui/command";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import CreateWorkspaceDialog from "@/app/workspaces/_components/CreateWorkspaceDialog";
import { useWorkspaces } from "../../_api/getWorkspaces";
import { useParams, useRouter } from "next/navigation";

const WorkspaceSwitcher = ({ className }) => {
  const router = useRouter();
  const params = useParams();
  const { workspaces } = useWorkspaces();
  const [open, setOpen] = React.useState(false);
  const [isDialogOpen, setIsDialogOpen] = React.useState(false);

  const selectedWorkspace = workspaces.find(
    ({ id }) => id === parseInt(params.id, 10)
  );

  const selectedOrPlaceholder = selectedWorkspace ?? workspaces[0];

  return (
    <CreateWorkspaceDialog isOpen={isDialogOpen} setIsOpen={setIsDialogOpen}>
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            variant="outline"
            role="combobox"
            aria-expanded={open}
            aria-label="Select a team"
            className={cn("w-[200px] justify-between", className)}
          >
            <Avatar className="mr-2 h-5 w-5">
              <AvatarImage
                src={selectedOrPlaceholder.thumbnailUrl}
                alt={selectedOrPlaceholder.name}
                className="grayscale"
              />
              <AvatarFallback className="w-5 h-5 text-[8px]">SC</AvatarFallback>
            </Avatar>
            {selectedOrPlaceholder.name}
            <CaretSortIcon className="ml-auto h-4 w-4 shrink-0 opacity-50" />
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-[200px] p-0">
          <Command>
            <CommandList>
              <CommandInput placeholder="Szukaj..." />
              <CommandEmpty>Nie znalezino.</CommandEmpty>
              <CommandGroup heading="Obszar roboczy">
                {workspaces.map((workspace) => (
                  <CommandItem
                    key={workspace.id}
                    onSelect={() => {
                      router.push(`/workspaces/${workspace.id}`);
                      setOpen(false);
                    }}
                    className="text-sm"
                  >
                    <Avatar className="mr-2 h-5 w-5">
                      <AvatarImage
                        src={workspace.thumbnailUrl}
                        alt={workspace.name}
                        className="grayscale"
                      />
                      <AvatarFallback className="w-5 h-5 text-[8px]">
                        SC
                      </AvatarFallback>
                    </Avatar>
                    {workspace.name}
                    <CheckIcon
                      className={cn(
                        "ml-auto h-4 w-4",
                        selectedWorkspace &&
                          selectedWorkspace.id === workspace.id
                          ? "opacity-100"
                          : "opacity-0"
                      )}
                    />
                  </CommandItem>
                ))}
              </CommandGroup>
            </CommandList>
            <CommandSeparator />
            <CommandList>
              <CommandGroup>
                <CommandItem
                  onSelect={() => {
                    setOpen(false);
                    setIsDialogOpen(true);
                  }}
                >
                  <PlusCircledIcon className="mr-2 h-5 w-5" />
                  Utwórz
                </CommandItem>
              </CommandGroup>
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>
    </CreateWorkspaceDialog>
  );
};

export default WorkspaceSwitcher;
