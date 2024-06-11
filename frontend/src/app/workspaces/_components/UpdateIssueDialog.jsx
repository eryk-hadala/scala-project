import { Button } from "@/components/ui/button";
import {
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Form, FormField } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { ReloadIcon } from "@radix-ui/react-icons";
import React, { useEffect } from "react";
import { useForm } from "react-hook-form";
import { Textarea } from "@/components/ui/textarea";
import { useUpdateIssue } from "@/app/workspaces/_api/updateIssue";

const UpdateIssueDialog = ({ issue }) => {
  const form = useForm({
    defaultValues: { ...issue },
  });

  useEffect(() => {
    form.reset(issue);
  }, [form, issue]);

  const { mutateAsync, isPending } = useUpdateIssue();

  const onSubmit = (data) => mutateAsync(data).catch((e) => console.log(e));

  return (
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
              Aktualizuj
            </Button>
          </DialogFooter>
        </form>
      </Form>
    </DialogContent>
  );
};

export default UpdateIssueDialog;
