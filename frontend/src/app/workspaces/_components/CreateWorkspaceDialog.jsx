"use client";

import React from "react";
import { useForm } from "react-hook-form";

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogClose,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { useCreateWorkspace } from "../_api/createWorkspaces";
import { Form, FormField } from "@/components/ui/form";
import { ReloadIcon } from "@radix-ui/react-icons";

const CreateWorkspaceDialog = ({
  children,
  renderTrigger,
  isOpen,
  setIsOpen,
}) => {
  const form = useForm({
    defaultValues: {
      name: "",
      tier: "",
    },
  });

  const { mutateAsync, isPending } = useCreateWorkspace();

  const onSubmit = (data) => mutateAsync(data).catch((e) => console.log(e));

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      {children}
      <DialogTrigger asChild>{renderTrigger}</DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Utwórz obszar roboczy</DialogTitle>
          <DialogDescription className="mt-1.5">
            Dodaj nowy obszar roboczy do zarządzania zadaniami.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="flex flex-col gap-4"
          >
            <div className="space-y-4 py-2 pb-4">
              <FormField
                name="name"
                label="Nazwa"
                rules={{ required: "To pole jest wymagane" }}
              >
                <Input placeholder="Wymagane" />
              </FormField>
              <FormField
                name="tier"
                label="Plan subskrypcji"
                rules={{ required: "To pole jest wymagane" }}
              >
                <Select>
                  <SelectTrigger>
                    <SelectValue placeholder="Wybierz plan" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="free">
                      <span className="font-medium">Free</span> -{" "}
                      <span className="text-muted-foreground">
                        Próba na dwa tygodnie
                      </span>
                    </SelectItem>
                    <SelectItem value="pro">
                      <span className="font-medium">Pro</span> -{" "}
                      <span className="text-muted-foreground">
                        20zł/miesiąc na użytkownika
                      </span>
                    </SelectItem>
                  </SelectContent>
                </Select>
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

export default CreateWorkspaceDialog;
