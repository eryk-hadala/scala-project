"use client";

import { PlusCircledIcon, ReloadIcon } from "@radix-ui/react-icons";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { CheckIcon } from "lucide-react";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import { useState } from "react";
import { useSearch } from "../../_api/search";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { useMembers } from "../../_api/getMembers";
import { cn } from "@/lib/utils";
import { useInviteMember } from "../../_api/inviteMember";
import { useParams } from "next/navigation";

const Toolbar = ({ table }) => {
  const { id: workspaceId } = useParams();
  const [search, setSearch] = useState("");
  const { data, isLoading } = useSearch(search);
  const { members } = useMembers();
  const { mutate } = useInviteMember();

  return (
    <div className="flex items-center justify-between">
      <div className="flex flex-1 items-center space-x-2">
        <Input
          placeholder="Filtruj zadania..."
          value={table.getColumn("username")?.getFilterValue() ?? ""}
          onChange={(event) =>
            table.getColumn("username")?.setFilterValue(event.target.value)
          }
          className="h-8 w-[150px] lg:w-[250px]"
        />
      </div>

      <Popover>
        <PopoverTrigger asChild>
          <Button size="sm" className="h-8">
            <PlusCircledIcon className="mr-2 h-4 w-4" />
            Dodaj członka
          </Button>
        </PopoverTrigger>

        <PopoverContent className="w-[300px] p-0">
          <Command shouldFilter={false}>
            <CommandInput
              value={search}
              onValueChange={setSearch}
              placeholder="Szukaj..."
            />
            <CommandList>
              {search && (
                <>
                  <CommandEmpty>
                    {isLoading ? (
                      <ReloadIcon className="animate-spin mx-auto" />
                    ) : (
                      "Nie znaleziono."
                    )}
                  </CommandEmpty>
                  <CommandGroup>
                    {data.map(({ id, username, avatarUrl }) => (
                      <CommandItem
                        key={id}
                        onSelect={() => mutate({ workspaceId, userId: id })}
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
                            members.find(({ id: userId }) => userId === id)
                              ? "opacity-100"
                              : "opacity-0"
                          )}
                        />
                      </CommandItem>
                    ))}
                  </CommandGroup>
                </>
              )}
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>
    </div>
  );
};

export default Toolbar;
