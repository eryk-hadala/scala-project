"use client";

import * as React from "react";
import { useForm } from "react-hook-form";

import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Form, FormField } from "@/components/ui/form";
import { useSignUp } from "@/app/auth/_api/signUp";
import { ReloadIcon } from "@radix-ui/react-icons";

const SignUpForm = ({ className, ...props }) => {
  const form = useForm({
    defaultValues: {
      email: "",
      username: "",
      password: "",
      "password-repeat": "",
    },
  });

  const { mutateAsync, isPending } = useSignUp();

  const onSubmit = (data) => mutateAsync(data).catch((e) => console.log(e));

  return (
    <div className={cn("grid gap-6", className)} {...props}>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)}>
          <div className="grid gap-4">
            <div className="flex flex-col sm:flex-row gap-4">
              <FormField
                name="email"
                label="Adres email"
                className="flex-1"
                rules={{
                  required: "To pole jest wymagane",
                  pattern: {
                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                    message: "Niepoprawny adres email",
                  },
                }}
              >
                <Input placeholder="name@example.com" />
              </FormField>
              <FormField
                name="username"
                label="Username"
                className="flex-1"
                rules={{ required: "To pole jest wymagane" }}
              >
                <Input placeholder="Wymagane" />
              </FormField>
            </div>
            <div className="flex flex-col sm:flex-row gap-4">
              <FormField
                name="password"
                label="Hasło"
                className="flex-1"
                rules={{
                  required: "To pole jest wymagane",
                  minLength: { value: 3, message: "Minimum 3 znaki" },
                }}
              >
                <Input type="password" placeholder="Wymagane" />
              </FormField>
              <FormField
                name="password-repeat"
                label="Powtórz Hasło"
                className="flex-1"
                rules={{
                  required: "To pole jest wymagane",
                  validate: (value) => {
                    if (form.watch("password") != value) {
                      return "Twoje hasła nie pasują";
                    }
                  },
                }}
              >
                <Input type="password" placeholder="Wymagane" />
              </FormField>
            </div>
            <Button className="mt-4" disabled={isPending}>
              {isPending && <ReloadIcon className="animate-spin" />}
              Zarejestruj się
            </Button>
          </div>
        </form>
      </Form>
    </div>
  );
};

export default SignUpForm;
