"use client";

import { useAuthenticate } from "@/app/auth/_api/authenticate";
import { ReloadIcon } from "@radix-ui/react-icons";
import { redirect } from "next/navigation";

export default function RootLayout({ children }) {
  const { user, isLoading, isError } = useAuthenticate();

  if (isLoading)
    return (
      <div className="flex items-center justify-center h-full">
        <ReloadIcon className="animate-spin" width={40} height={40} />
      </div>
    );

  if (!user || isError) return redirect("/auth/sign-in");

  return children;
}
