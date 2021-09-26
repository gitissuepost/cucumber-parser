#Author: your.email@your.domain.com
#Keywords Summary :
#Feature: List of scenarios.
#Scenario: Business rule through list of steps with arguments.
#Given: Some precondition step
#When: Some key actions
#Then: To observe outcomes or validation
#And,But: To enumerate more Given,When,Then steps
#Scenario Outline: List of steps for data-driven as an Examples and <placeholder>
#Examples: Container for s table
#Background: List of steps run before each of the scenarios
#""" (Doc Strings)
#| (Data Tables)
#@ (Tags/Labels):To group Scenarios
#<> (placeholder)
#""
## (Comments)
#Sample Feature Definition Template
Feature: Visit career guide page in career.guru99.com
@Gotcha
  Scenario: Visit career.guru99.com
    Given I am on career.guru99.com
    		|a|b|c|
    		|a1|b1|c1|
    When I click on career guide menu
    		|a|b|c|
    		|a1|b2|c1|
    Then I should see career guide page
    		|a|b|c|
    		|a1|b1|c1|

