"use client";

import { useForm } from "react-hook-form";

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import SettingsDialogSidebar from "@/app/workspaces/_components/Sidebar/SettingsDialogSidebar";
import { Form, FormField } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { useSingleWorkspace } from "../../_api/getWorkspaces";
import { ReloadIcon } from "@radix-ui/react-icons";
import { useUpdateWorkspace } from "../../_api/updateWorkspace";

const SettingsDialog = ({ children }) => {
  const { workspace } = useSingleWorkspace();
  const { mutateAsync, isPending } = useUpdateWorkspace();
  const form = useForm({
    defaultValues: {
      name: workspace.name,
      thumbnailUrl: workspace.thumbnailUrl,
      tier: workspace.tier,
    },
  });

  const onSubmit = (data) => mutateAsync(data).catch((e) => console.log(e));

  return (
    <Dialog>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent className="xl:max-w-7xl px-10 py-8">
        <DialogHeader>
          <DialogTitle>Ustawienia</DialogTitle>
          <DialogDescription className="mt-1.5">
            Zarządzaj ustawieniami konta i konfiguruj preferencje poczty e-mail.
          </DialogDescription>
        </DialogHeader>
        <Separator className="my-2" />
        <div className="flex text-left flex-col gap-x-8 gap-y-4 lg:flex-row">
          <SettingsDialogSidebar
            items={[
              { href: "/", title: "Główne" },
              { href: "/2", title: "Uprawnienia" },
            ]}
          />
          <Form {...form}>
            <div className="flex-1 lg:max-w-2xl">
              <form onSubmit={form.handleSubmit(onSubmit)}>
                <h3 className="text-base mb-0.5 font-medium">Profile</h3>
                <p className="text-[13px] text-muted-foreground">
                  This is how others will see you on the site.
                </p>
                <Separator className="my-4" />
                <div className="space-y-8">
                  <FormField
                    name="name"
                    label="Name"
                    description="This is your public display name. It can be your real name or a pseudonym. You can only change this once every 30 days."
                  >
                    <Input placeholder="Wymagane" />
                  </FormField>
                  <FormField
                    name="thumbnailUrl"
                    label="Miniaturka"
                    description="You can manage verified email addresses in your email settings."
                  >
                    <Input placeholder="http://..." />
                  </FormField>
                  <FormField
                    name="tier"
                    label="Plan"
                    description="You can manage verified email addresses in your email settings."
                  >
                    <Input disabled />
                  </FormField>
                  <Button type="submit" disabled={isPending}>
                    {isPending && <ReloadIcon className="animate-spin" />}
                    Aktualizuj
                  </Button>
                </div>
              </form>
            </div>
          </Form>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default SettingsDialog;
