
CREATE TABLE "applications"."ClientRequest" (
    "id" bigserial NOT NULL,
    "version" INT,
    "created" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "phone" CHARACTER VARYING(50) NOT NULL,
    "email" CHARACTER VARYING(200) NOT NULL,
    "monthlyIncome" NUMERIC(10,2) NOT NULL,
    "monthlyExpenses" NUMERIC(10,2) NOT NULL,
    "maritalStatus" CHARACTER VARYING(50) NOT NULL,
    "dependents" INT NOT NULL,
    "agreeToBeScored" BOOL NOT NULL,
    "amount" NUMERIC(10,2) NOT NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE "applications"."BankOffer" (
	"id" bigserial NOT NULL,
	"version" INT,
	"created" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	"extId" CHARACTER VARYING(50),
	"status" CHARACTER VARYING(50) NOT NULL,
	"statusProc" CHARACTER VARYING(50) NOT NULL,
	"bankName" CHARACTER VARYING(100) NOT NULL,
	"clientRequest_id" INT8 NOT NULL,
	"offerMonthlyPayment"  NUMERIC(10,2),
	"offerTotalRepayment"  NUMERIC(10,2),
	"offerNumberOfPayments" INT,
	"offerAnnualRate" NUMERIC(6,3),
	"offerFirstRepayment" CHARACTER VARYING(50),
	 FOREIGN KEY ("clientRequest_id")
        REFERENCES "applications"."ClientRequest" ("id")
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    PRIMARY KEY ("id")
);