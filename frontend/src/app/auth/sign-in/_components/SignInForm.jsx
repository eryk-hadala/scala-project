"use client";

import * as React from "react";
import { useForm } from "react-hook-form";
import { ReloadIcon } from "@radix-ui/react-icons";

import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Form, FormField } from "@/components/ui/form";
import { useSignIn } from "@/app/auth/_api/signIn";

const SignInForm = ({ className, ...props }) => {
  const form = useForm({ defaultValues: { email: "", password: "" } });
  const { mutateAsync, isPending } = useSignIn();

  const onSubmit = (data) => mutateAsync(data).catch((e) => console.log(e));

  return (
    <div className={cn("grid gap-6", className)} {...props}>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)}>
          <div className="grid gap-4">
            <FormField
              name="email"
              label="Adres email"
              rules={{
                required: "To pole jest wymagane",
                pattern: {
                  value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                  message: "Niepoprawny adres email",
                },
              }}
            >
              <Input
                placeholder="name@example.com"
                autoCapitalize="none"
                autoComplete="email"
                autoCorrect="off"
              />
            </FormField>
            <FormField
              name="password"
              label="Hasło"
              rules={{
                required: "To pole jest wymagane",
                minLength: { value: 3, message: "Minimum 3 znaki" },
              }}
            >
              <Input
                type="password"
                placeholder="Wymagane"
                autoCapitalize="none"
                autoComplete="email"
                autoCorrect="off"
              />
            </FormField>
            <Button className="mt-4" disabled={isPending}>
              {isPending && <ReloadIcon className="animate-spin" />}
              Zaloguj się
            </Button>
          </div>
        </form>
        <div className="relative">
          <div className="absolute inset-0 flex items-center">
            <span className="w-full border-t" />
          </div>
          <div className="relative flex justify-center text-xs uppercase">
            <span className="bg-background px-2 text-muted-foreground">
              Lub kontynuuj z
            </span>
          </div>
        </div>
        <Button variant="outline" type="button" disabled={isPending}>
          GitHub
        </Button>
      </Form>
    </div>
  );
};

export default SignInForm;
