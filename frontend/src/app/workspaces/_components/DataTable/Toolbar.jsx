"use client";

import { Cross2Icon, PlusCircledIcon } from "@radix-ui/react-icons";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

import Filter from "@/app/workspaces/_components/DataTable/Filter";
import { statuses, priorities } from "@/app/workspaces/_lib/utils";
import CreateIssueDialog from "../CreateIssueDialog";

const Toolbar = ({ table }) => {
  const isFiltered = table.getState().columnFilters.length > 0;

  return (
    <div className="flex items-center justify-between">
      <div className="flex flex-1 items-center space-x-2">
        <Input
          placeholder="Filtruj zadania..."
          value={table.getColumn("title")?.getFilterValue() ?? ""}
          onChange={(event) =>
            table.getColumn("title")?.setFilterValue(event.target.value)
          }
          className="h-8 w-[150px] lg:w-[250px]"
        />
        {table.getColumn("status") && (
          <Filter
            column={table.getColumn("status")}
            title="Status"
            options={statuses}
          />
        )}
        {table.getColumn("priority") && (
          <Filter
            column={table.getColumn("priority")}
            title="Priorytet"
            options={priorities}
          />
        )}
        {isFiltered && (
          <Button
            variant="ghost"
            onClick={() => table.resetColumnFilters()}
            className="h-8 px-2 lg:px-3"
          >
            Resetuj
            <Cross2Icon className="ml-2 h-4 w-4" />
          </Button>
        )}
      </div>
      <CreateIssueDialog
        renderTrigger={
          <Button size="sm" className="h-8">
            <PlusCircledIcon className="mr-2 h-4 w-4" />
            Dodaj zadanie
          </Button>
        }
      />
    </div>
  );
};

export default Toolbar;
