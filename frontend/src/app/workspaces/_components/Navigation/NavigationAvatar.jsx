"use client";

import { useForm } from "react-hook-form";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuShortcut,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogFooter,
  DialogTitle,
  DialogTrigger,
  DialogClose,
} from "@/components/ui/dialog";
import { Form, FormField } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import {
  useAuthenticate,
  useAuthenticateMutation,
} from "@/app/auth/_api/authenticate";
import { ReloadIcon } from "@radix-ui/react-icons";
import { useSignOut } from "@/app/auth/_api/signOut";

const NavigationAvatar = () => {
  const { user } = useAuthenticate();
  const { mutateAsync, isPending } = useAuthenticateMutation();
  const { mutate: signOut, isPending: isSignOutPending } = useSignOut();
  const form = useForm({
    defaultValues: { username: user.username, avatarUrl: user.avatarUrl },
  });

  const onSubmit = (data) => mutateAsync(data).catch((e) => console.log(e));

  return (
    <Dialog>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" className="relative h-8 w-8 rounded-full">
            <Avatar className="h-8 w-8">
              <AvatarImage src={user.avatarUrl} alt="" />
              <AvatarFallback>Aa</AvatarFallback>
            </Avatar>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent className="w-56" align="end" forceMount>
          <DropdownMenuLabel className="font-normal">
            <div className="flex flex-col space-y-1">
              <p className="text-sm font-medium leading-none">
                {user.username}
              </p>
              <p className="text-xs leading-none text-muted-foreground">
                {user.email}
              </p>
            </div>
          </DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuGroup>
            <DropdownMenuItem>
              Profil
              <DropdownMenuShortcut>⇧⌘P</DropdownMenuShortcut>
            </DropdownMenuItem>
            <DialogTrigger asChild>
              <DropdownMenuItem>
                Ustawienia
                <DropdownMenuShortcut>⌘B</DropdownMenuShortcut>
              </DropdownMenuItem>
            </DialogTrigger>
          </DropdownMenuGroup>
          <DropdownMenuSeparator />
          <DropdownMenuItem
            onSelect={(event) => {
              event.preventDefault();
              signOut();
            }}
            className="flex gap-2"
          >
            Wyloguj się
            {isSignOutPending && <ReloadIcon className="animate-spin" />}
            <DropdownMenuShortcut>⇧⌘Q</DropdownMenuShortcut>
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <DialogContent>
        <DialogHeader>
          <DialogTitle>Ustawienia</DialogTitle>
          <DialogDescription className="mt-1.5">
            Zarządzaj ustawieniami konta i konfiguruj preferencje poczty e-mail.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="flex flex-col gap-4"
          >
            <div className="space-y-4 py-2 pb-4">
              <FormField name="avatarUrl" label="Avatar">
                <Input placeholder="http://..." />
              </FormField>
              <FormField name="username" label="Username">
                <Input placeholder="Wymagane" />
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
    </Dialog>
  );
};

export default NavigationAvatar;
