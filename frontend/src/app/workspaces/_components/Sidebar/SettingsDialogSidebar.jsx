"use client";

import Link from "next/link";

import { cn } from "@/lib/utils";
import { buttonVariants } from "@/components/ui/button";

const SettingsDialogSidebar = ({ className, items, ...props }) => {
  return (
    <nav
      className={cn("flex w-60 lg:flex-col space-y-1", className)}
      {...props}
    >
      {items.map((item, index) => (
        <Link
          key={item.href}
          href=""
          className={cn(
            buttonVariants({ variant: "ghost" }),
            index === 0 ? "bg-muted hover:bg-muted" : "hover:bg-muted",
            "justify-start"
          )}
        >
          {item.title}
        </Link>
      ))}
    </nav>
  );
};

export default SettingsDialogSidebar;
