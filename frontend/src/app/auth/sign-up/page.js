import Link from "next/link";
import SignUpForm from "@/app/auth/sign-up/_components/SignUpForm";

const SignUpPage = () => {
  return (
    <div className="flex flex-col h-full overflow-auto">
      <div className="flex-1" />
      <div className="mx-auto py-8 px-4 flex w-full flex-col justify-center space-y-6 sm:w-[482px]">
        <div className="flex flex-col space-y-2 text-center">
          <h1 className="text-2xl font-semibold tracking-tight">
            Utwórz nowe konto
          </h1>
          <p className="text-sm text-muted-foreground">
            Enter your email below to create your account
          </p>
        </div>
        <SignUpForm />
        <p className="px-8 text-center text-sm text-muted-foreground">
          Masz jeszcze konta?{" "}
          <Link
            href="/auth/sign-in"
            className="underline underline-offset-4 hover:text-primary"
          >
            Przejdź do logowania
          </Link>{" "}
        </p>
      </div>
      <div className="flex-1" />
    </div>
  );
};

export default SignUpPage;
