"use client";

import {
  CheckIcon,
  DotsHorizontalIcon,
  ReloadIcon,
} from "@radix-ui/react-icons";

import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuRadioGroup,
  DropdownMenuRadioItem,
  DropdownMenuSeparator,
  DropdownMenuShortcut,
  DropdownMenuSub,
  DropdownMenuSubContent,
  DropdownMenuSubTrigger,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

import { labels, priorities, statuses } from "@/app/workspaces/_lib/utils";
import { useRemoveIssue } from "@/app/workspaces/_api/removeIssue";
import { useUpdateIssue } from "@/app/workspaces/_api/updateIssue";
import { useCreateIssue } from "@/app/workspaces/_api/createIssue";
import UpdateIssueDialog from "../UpdateIssueDialog";
import { Dialog, DialogTrigger } from "@/components/ui/dialog";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import { useMembers } from "@/app/workspaces/_api/getMembers";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { cn } from "@/lib/utils";
import { useAssigneeIssue } from "@/app/workspaces/_api/assigneeIssue";

const RowActions = ({ row }) => {
  const task = row.original;
  const { mutate: create } = useCreateIssue();
  const { mutate: remove } = useRemoveIssue();
  const { mutate: update } = useUpdateIssue();

  return (
    <Dialog>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button
            variant="ghost"
            className="flex h-8 w-8 p-0 data-[state=open]:bg-muted"
          >
            <DotsHorizontalIcon className="h-4 w-4" />
            <span className="sr-only">Open menu</span>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end" className="w-[160px]">
          <DialogTrigger asChild>
            <DropdownMenuItem>Edytuj</DropdownMenuItem>
          </DialogTrigger>
          <DropdownMenuItem onClick={() => create(task)}>
            Skopiuj
          </DropdownMenuItem>
          <DropdownMenuSeparator />
          <DropdownMenuSub>
            <DropdownMenuSubTrigger>Etykiety</DropdownMenuSubTrigger>
            <DropdownMenuSubContent>
              <DropdownMenuRadioGroup value={task.label}>
                {labels.map((label) => (
                  <DropdownMenuRadioItem
                    key={label}
                    value={label}
                    onClick={() => update({ ...task, label })}
                  >
                    {label}
                  </DropdownMenuRadioItem>
                ))}
              </DropdownMenuRadioGroup>
            </DropdownMenuSubContent>
          </DropdownMenuSub>
          <DropdownMenuSub>
            <DropdownMenuSubTrigger>Status</DropdownMenuSubTrigger>
            <DropdownMenuSubContent>
              <DropdownMenuRadioGroup value={task.status}>
                {statuses.map(({ value, label }) => (
                  <DropdownMenuRadioItem
                    key={value}
                    value={value}
                    onClick={() => update({ ...task, status: value })}
                  >
                    {label}
                  </DropdownMenuRadioItem>
                ))}
              </DropdownMenuRadioGroup>
            </DropdownMenuSubContent>
          </DropdownMenuSub>
          <DropdownMenuSub>
            <DropdownMenuSubTrigger>Priorytet</DropdownMenuSubTrigger>
            <DropdownMenuSubContent>
              <DropdownMenuRadioGroup value={task.priority}>
                {priorities.map(({ value, label }) => (
                  <DropdownMenuRadioItem
                    key={value}
                    value={value}
                    onClick={() => update({ ...task, priority: value })}
                  >
                    {label}
                  </DropdownMenuRadioItem>
                ))}
              </DropdownMenuRadioGroup>
            </DropdownMenuSubContent>
          </DropdownMenuSub>
          <DropdownMenuSeparator />
          <DropdownMenuSub>
            <DropdownMenuSubTrigger>Przypisani</DropdownMenuSubTrigger>
            <DropdownMenuSubContent>
              <UsersPicker issue={task} />
            </DropdownMenuSubContent>
          </DropdownMenuSub>
          <DropdownMenuSeparator />
          <DropdownMenuItem onClick={() => remove(task)}>
            Usuń
            <DropdownMenuShortcut>⌘⌫</DropdownMenuShortcut>
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
      <UpdateIssueDialog issue={task} />
    </Dialog>
  );
};

const UsersPicker = ({ issue }) => {
  const { members, isLoading } = useMembers();
  const { mutate } = useAssigneeIssue();

  return (
    <Command>
      <CommandInput placeholder="Szukaj..." />
      <CommandList>
        <CommandEmpty>
          {isLoading ? (
            <ReloadIcon className="animate-spin mx-auto" />
          ) : (
            "Nie znaleziono."
          )}
        </CommandEmpty>
        <CommandGroup>
          {members.map(({ id, username, avatarUrl }) => (
            <CommandItem
              key={id}
              onSelect={() => {
                const newAssigneeIds = issue.assignees.map(({ id }) => id);
                mutate({
                  ...issue,
                  assignees: newAssigneeIds.includes(id)
                    ? newAssigneeIds.filter((userId) => userId !== id)
                    : [...newAssigneeIds, id],
                });
              }}
              className="text-sm"
            >
              <Avatar className="mr-2 h-5 w-5">
                <AvatarImage
                  src={avatarUrl}
                  alt={username}
                  className="grayscale"
                />
                <AvatarFallback className="w-5 h-5 text-[8px]">
                  Aa
                </AvatarFallback>
              </Avatar>
              {username}
              <CheckIcon
                className={cn(
                  "ml-auto h-4 w-4",
                  issue.assignees.find(({ id: userId }) => userId === id)
                    ? "opacity-100"
                    : "opacity-0"
                )}
              />
            </CommandItem>
          ))}
        </CommandGroup>
      </CommandList>
    </Command>
  );
};

export default RowActions;
