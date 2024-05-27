import { cn } from "@/lib/utils";
import React from "react";

const Bone = ({ size = "sm", className }) => {
  return (
    <div
      className={cn(
        `${size === "sm" ? "h-5 w-[108px]" : "h-6 w-36"} rounded bg-slate-200`,
        className
      )}
    ></div>
  );
};

const Circle = ({ size = "sm", className }) => {
  return (
    <div className={cn("h-5 w-5 rounded-full bg-slate-200", className)}></div>
  );
};

const SidebarSkeleton = () => {
  return (
    <div className="pb-12 w-64 border-r overflow-auto no-scrollbar">
      <div className="space-y-8 py-4">
        <div className="px-3 py-2">
          {/* <h2 className="mb-2 px-4 text-lg font-semibold tracking-tight">
            Planowanie
          </h2> */}
          <Bone size="lg" className="mb-6 mx-4" />
          <div className="space-y-6">
            {[0, 0, 0, 0].map((_, index) => (
              <div key={index} className="px-4 flex gap-4">
                <Circle />
                <Bone />
              </div>
            ))}
          </div>
        </div>
        <div className="px-3 py-2">
          <Bone size="lg" className="mb-6 mx-4" />
          <div className="space-y-6">
            {[0, 0].map((_, index) => (
              <div key={index} className="px-4 flex gap-4">
                <Circle />
                <Bone />
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default SidebarSkeleton;
