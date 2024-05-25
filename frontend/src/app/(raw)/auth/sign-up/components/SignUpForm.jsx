"use client";

import * as React from "react";
import { useForm } from "react-hook-form";

import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Form, FormField } from "@/components/ui/form";

const SignUpForm = ({ className, ...props }) => {
  const form = useForm();

  const onSubmit = (data) => {
    console.log(data);
  };

  return (
    <div className={cn("grid gap-6", className)} {...props}>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)}>
          <div className="grid gap-4">
            <FormField name="email" label="Adres email">
              <Input placeholder="name@example.com" />
            </FormField>
            <FormField name="username" label="Username">
              <Input placeholder="Wymagane" />
            </FormField>
            <Button className="mt-4">
              {/* {isLoading && (
              <Icons.spinner className="mr-2 h-4 w-4 animate-spin" />
            )} */}
              Zarejestruj się
            </Button>
          </div>
        </form>
      </Form>
    </div>
  );
};

export default SignUpForm;
