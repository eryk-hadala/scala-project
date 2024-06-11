import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Form, FormField } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { ReloadIcon } from "@radix-ui/react-icons";
import React from "react";
import { useCreateIssue } from "@/app/workspaces/_api/createIssue";
import { useForm } from "react-hook-form";
import { labels, priorities } from "../_lib/utils";
import { Textarea } from "@/components/ui/textarea";
import { useParams } from "next/navigation";

const CreateIssueDialog = ({ renderTrigger, isOpen, setIsOpen }) => {
  const { id } = useParams();
  const form = useForm({
    defaultValues: {
      workspaceId: id,
      title: "",
      status: "todo",
      label: "",
      priority: "",
      content: "",
    },
  });

  const { mutateAsync, isPending } = useCreateIssue();

  const onSubmit = (data) =>
    mutateAsync(data)
      .then(() => {
        form.reset();
        setIsOpen(false);
      })
      .catch((e) => console.log(e));

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>{renderTrigger}</DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Dodaj nowe zadanie</DialogTitle>
          <DialogDescription className="mt-1.5">
            Dodaj nowe zadanie i przypisz odpowiedniego członka.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="flex flex-col gap-4"
          >
            <div className="space-y-4 py-2 pb-4">
              <FormField
                name="title"
                label="Tytuł"
                rules={{ required: "To pole jest wymagane" }}
              >
                <Input placeholder="Wymagane" />
              </FormField>
              <FormField
                name="label"
                label="Etykieta"
                rules={{ required: "To pole jest wymagane" }}
              >
                <Select>
                  <SelectTrigger>
                    <SelectValue placeholder="Wybierz etykiete" />
                  </SelectTrigger>
                  <SelectContent>
                    {labels.map((label) => (
                      <SelectItem key={label} value={label}>
                        <span className="font-medium">{label}</span>
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </FormField>
              <FormField
                name="priority"
                label="Priorytet"
                rules={{ required: "To pole jest wymagane" }}
              >
                <Select>
                  <SelectTrigger>
                    <SelectValue placeholder="Wybierz priorytet" />
                  </SelectTrigger>
                  <SelectContent>
                    {priorities.map((priority) => (
                      <SelectItem key={priority.value} value={priority.value}>
                        <span className="font-medium">{priority.label}</span>
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </FormField>
              <FormField
                name="content"
                label="Zawartość"
                rules={{ required: "To pole jest wymagane" }}
              >
                <Textarea placeholder="Wymagane" />
              </FormField>
            </div>
            <DialogFooter>
              <DialogClose asChild>
                <Button variant="outline">Anuluj</Button>
              </DialogClose>
              <Button type="submit" disabled={isPending}>
                {isPending && <ReloadIcon className="animate-spin" />}
                Utwórz
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
};

export default CreateIssueDialog;
