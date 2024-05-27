import React from "react";
import { cn } from "@/lib/utils";
import NavigationAvatar from "./NavigationAvatar";
import Search from "./Search";
import WorkspaceSwitcher from "./WorkspaceSwitcher";

const Bone = ({ size = "sm", className }) => {
  return (
    <div
      className={cn(
        `${size === "sm" ? "h-5 w-20" : "h-6 w-36"} rounded bg-slate-200`,
        className
      )}
    ></div>
  );
};

const Circle = ({ size = "sm", className }) => {
  return (
    <div
      className={cn(
        `${size === "sm" ? "h-5 w-5" : "h-6 w-6"} rounded-full bg-slate-200`,
        className
      )}
    ></div>
  );
};

const NavigationSkeleton = ({ variant }) => {
  return (
    <nav className="border-b">
      <div className="flex h-16 items-center px-4">
        {variant === "second" ? (
          <WorkspaceSwitcher />
        ) : (
          <div className="pl-2 flex gap-4">
            <Circle size="lg" />
            <Bone size="lg" />
          </div>
        )}
        <div className="flex gap-6 mx-8">
          {[0, 0, 0, 0].map((_, index) => (
            <Bone key={index} />
          ))}
        </div>
        <div className="ml-auto flex items-center space-x-4">
          {variant === "second" ? (
            <Search />
          ) : (
            <Bone size="lg" className="w-[280px]" />
          )}
          <NavigationAvatar />
        </div>
      </div>
    </nav>
  );
};

export default NavigationSkeleton;
