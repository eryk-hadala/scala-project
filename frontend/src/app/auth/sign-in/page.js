import Link from "next/link";
import SignInForm from "@/app/auth/sign-in/_components/SignInForm";

const SignInPage = () => {
  return (
    <div className="flex flex-col h-full overflow-auto">
      <div className="flex-1" />
      <div className="mx-auto py-8 px-4 flex w-full flex-col justify-center space-y-6 sm:w-[382px]">
        <div className="flex flex-col space-y-2 text-center">
          <h1 className="text-2xl font-semibold tracking-tight">Zaloguj się</h1>
          <p className="text-sm text-muted-foreground">
            Enter your email below to create your account
          </p>
        </div>
        <SignInForm />
        <p className="px-8 text-center text-sm text-muted-foreground">
          Nie masz jeszcze konta?{" "}
          <Link
            href="/auth/sign-up"
            className="underline underline-offset-4 hover:text-primary"
          >
            Załóż konto
          </Link>{" "}
        </p>
      </div>
      <div className="flex-1" />
    </div>
  );
};

export default SignInPage;
