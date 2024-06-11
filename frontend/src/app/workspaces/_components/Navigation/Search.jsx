import { Input } from "@/components/ui/input";
import { useState } from "react";
import { useSearch } from "../../_api/search";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { ReloadIcon } from "@radix-ui/react-icons";

const Search = () => {
  const [search, setSearch] = useState("");
  const [isFocused, setIsFocused] = useState(false);

  const { data, isLoading } = useSearch(search);

  return (
    <div
      className="relative"
      onFocus={() => setIsFocused(true)}
      onBlur={() => setIsFocused(false)}
    >
      <Input
        type="search"
        placeholder="Szukaj..."
        className="lg:w-[300px]"
        onChange={(event) => setSearch(event.target.value)}
      />
      {isFocused && search && (
        <div className="absolute mt-1 z-50 w-full p-1 rounded-md border bg-popover text-popover-foreground shadow-md outline-none data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0 data-[state=closed]:zoom-out-95 data-[state=open]:zoom-in-95 data-[side=bottom]:slide-in-from-top-2 data-[side=left]:slide-in-from-right-2 data-[side=right]:slide-in-from-left-2 data-[side=top]:slide-in-from-bottom-2">
          {isLoading ? (
            <div className="flex items-center justify-center h-[56px]">
              <ReloadIcon className="animate-spin" />
            </div>
          ) : !data.length ? (
            <div className="flex items-center justify-center h-[56px] text-sm">
              Nie znaleziono.
            </div>
          ) : (
            <div>
              <h3 className="px-2 py-1.5 text-xs font-medium text-muted-foreground">
                Użytkownicy
              </h3>
              {data.map((user) => (
                <div key={user.id} className="px-2 py-1.5 text-sm flex">
                  <Avatar className="mr-2 h-5 w-5">
                    <AvatarImage
                      src={user.avatarUrl}
                      alt={user.username}
                      className="grayscale"
                    />
                    <AvatarFallback className="w-5 h-5 text-[8px]">
                      Aa
                    </AvatarFallback>
                  </Avatar>
                  {user.username}
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default Search;
