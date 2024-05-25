import Navigation from "@/components/Navigation/Navigation";
import Sidebar from "@/components/Sidebar/Sidebar";
import "@/styles/globals.css";

export default function RootLayout({ children }) {
  return (
    <div className="flex flex-col h-full">
      <Navigation />
      <div className="flex flex-1 overflow-hidden">
        <Sidebar className="flex-shrink-0" />
        <main className="flex-1 overflow-auto">{children}</main>
      </div>
    </div>
  );
}
